local function init_space()

    -- Book space
    local test_space = box.schema.space.create(
            'test_space',
            {
                format = {
                    { name = 'id', type = 'unsigned' },
                    { name = 'bucket_id', type = 'unsigned' },
                    { name = 'unique_key', type = 'string' },
                    { name = 'book_name', type = 'string' },
                    { name = 'author', type = 'string' },
                    { name = 'year', type = 'unsigned', is_nullable = true },
                    { name = 'issuerAddress', type = 'any', is_nullable = true },
                    { name = 'storeAddresses', type = 'array', is_nullable = true },
                    { name = 'readers', type = 'array', is_nullable = true },
                    { name = 'issueDate', type = 'string', is_nullable = true },
                },
                if_not_exists = true,
            }
    )

    test_space:create_index('id', {
        parts = { 'id' },
        if_not_exists = true,
    })

    test_space:create_index('inx_author', {
        type = 'tree',
        unique = false,
        parts = { 'author' },
        if_not_exists = true,
    })

    test_space:create_index('bucket_id', {
        parts = { 'bucket_id' },
        unique = false,
        if_not_exists = true,
    })

    test_space:create_index('name', {
        type = 'tree',
        parts = { 'book_name' },
        unique = true,
        if_not_exists = true,
    })

    -- Customer space
    local customers = box.schema.space.create(
            'customers',
            {
                format = {
                    -- {name = 'uuid', type = 'uuid'}, -- blocked by https://github.com/tarantool/crud/issues/84
                    { name = 'id', type = 'unsigned' },
                    { name = 'bucket_id', type = 'unsigned' },
                    { name = 'name', type = 'string' },
                    { name = 'tags', type = 'array' },
                    { name = 'addresses', type = 'any' },
                    { name = 'foreignAddresses', type = 'array', is_nullable = true },
                    { name = 'favouriteBooks', type = 'array', is_nullable = true },
                    { name = 'lastVisitTime', type = 'unsigned', is_nullable = true },
                },
                if_not_exists = true,
            }
    )

    customers:create_index('id', {
        parts = { 'id' },
        if_not_exists = true,
    })

    customers:create_index('bucket_id', {
        parts = { 'bucket_id' },
        unique = false,
        if_not_exists = true,
    })

    --BookTranslation space
    local book_translation = box.schema.space.create(
            'book_translation',
            {
                format = {
                    { name = 'id', type = 'unsigned' },
                    { name = 'bucket_id', type = 'unsigned' },
                    { name = 'language', type = 'string' },
                    { name = 'edition', type = 'integer' },
                    { name = 'translator', type = 'string' },
                    { name = 'comments', type = 'string', is_nullable = true },
                },
                if_not_exists = true,
            }
    )

    book_translation:create_index('id', {
        parts = { 'id', 'language', 'edition' },
        if_not_exists = true,
    })

    book_translation:create_index('bucket_id', {
        parts = { 'bucket_id' },
        unique = false,
        if_not_exists = true,
    })

    --BookStore space
    local book_store = box.schema.space.create(
            'book_store',
            {
                format = {
                    { name = 'id', type = 'unsigned' },
                    { name = 'bucket_id', type = 'unsigned' },
                    { name = 'received_at', type = 'unsigned' },
                    { name = 'store_number', type = 'integer' },
                },
                if_not_exists = true,
            }
    )

    book_store:create_index('id', {
        parts = { 'id', 'received_at'},
        if_not_exists = true,
    })

    book_store:create_index('bucket_id', {
        parts = { 'bucket_id' },
        unique = false,
        if_not_exists = true,
    })

    local test_custom_converter_space = box.schema.space.create(
            'test_custom_converter_space',
            {
                format = {
                    { name = 'id', type = 'unsigned' },
                    { name = 'bucket_id', type = 'unsigned' },
                    { name = 'test', type = 'number' }
                },
                if_not_exists = true,
            }
    )

    test_custom_converter_space:create_index('id', { parts = { 'id' }, if_not_exists = true, })
    test_custom_converter_space:create_index('bucket_id', {
        parts = { 'bucket_id' },
        unique = false,
        if_not_exists = true,
    })

    local test_simple_object = box.schema.space.create(
            'test_simple_object',
            {
                format = {
                    { name = 'testId', type = 'unsigned' },
                    { name = 'testBoolean', type = 'boolean' },
                    { name = 'testString', type = 'string' },
                    { name = 'testInteger', type = 'integer' },
                    { name = 'testDouble', type = 'double' },
                    { name = 'bucket_id', type = 'unsigned' },
                },
                if_not_exists = true,
            }
    )

    test_simple_object:create_index('testId', { parts = { 'testId' }, if_not_exists = true, })
    test_simple_object:create_index('bucket_id', {
        parts = { 'bucket_id' },
        unique = false,
        if_not_exists = true,
    })

    local test_get_object_space = box.schema.space.create(
            'test_get_object_space',
            {
                format = {
                    { name = 'id', type = 'unsigned' },
                    { name = 'bucket_id', type = 'unsigned' },
                    { name = 'test', type = 'number' }
                },
                if_not_exists = true,
            }
    )

    test_get_object_space:create_index('id', { parts = { 'id' }, if_not_exists = true, })
    test_get_object_space:create_index('bucket_id', {
        parts = { 'bucket_id' },
        unique = false,
        if_not_exists = true,
    })

    box.schema.space.create("dropped_space")
end

local function storage_get_space_format()
    local ddl = require('ddl')
    return ddl.get_schema()
end

local function find_books_by_name(names)
    local books = {}
    for _, name in pairs(names) do
        books[#books + 1] = box.space.test_space.index.name:select(name)[1]
    end
    return books
end

local function find_customer_by_address(address)
    return box.space.customers:pairs()
              :filter(function(c) return c.addresses.home.city == address.city end)
              :totable()
end

local function find_customer_by_book(book)
    return box.space.customers:pairs()
              :filter(function(c) return c.favouriteBooks[1].name == book.name end)
              :totable()
end

local function find_book_by_address(address)
    return box.space.test_space:pairs()
              :map(function(t) return t:tomap() end)
              :filter(function(b) return b.issuerAddress ~= nil and b.issuerAddress.city == address.city end)
              :totable()
end

local function find_book_by_book(book)
    return box.space.test_space:pairs()
              :map(function(t) return t:tomap() end)
              :filter(function(b) return b.id == book.id end)
              :totable()
end

local function drop_space(space_name)
    return box.space[space_name]:drop()
end

local function init(opts)
    if opts.is_master then
        init_space()

        box.schema.func.create('storage_get_space_format', { if_not_exists = true })
    end

    rawset(_G, 'storage_get_space_format', storage_get_space_format)
    rawset(_G, 'find_books_by_name', find_books_by_name)
    rawset(_G, 'find_customer_by_address', find_customer_by_address)
    rawset(_G, 'find_customer_by_book', find_customer_by_book)
    rawset(_G, 'find_book_by_address', find_book_by_address)
    rawset(_G, 'find_book_by_book', find_book_by_book)
    rawset(_G, 'drop_space', drop_space)
    rawset(_G, 'ddl', { get_schema = require('ddl').get_schema })

    return true
end

return {
    role_name = 'app.roles.api_storage',
    init = init,
    utils = {
        storage_get_space_format = storage_get_space_format,
        find_books_by_name = find_books_by_name,
        find_customer_by_address = find_customer_by_address,
    },
    dependencies = {
        'cartridge.roles.crud-storage'
    }
}
