
local function init_space()
    local test_space = box.schema.space.create(
        'test_space',
        {
            format = {
                {name = 'id', type = 'unsigned'},
                {name = 'bucket_id', type = 'unsigned'},
                {name = 'unique_key', type = 'string'},
                {name = 'book_name', type = 'string'},
                {name = 'author', type = 'string'},
                {name = 'year', type = 'unsigned',is_nullable=true},
            },
            if_not_exists = true,
        }
    )

    test_space:create_index('id', {
        parts = {'id'},
        if_not_exists = true,
    })

    test_space:create_index('inx_author', {
        type = 'tree',
        unique = false,
        parts = {'author'},
        if_not_exists = true,
    })

    test_space:create_index('bucket_id', {
        parts = {'bucket_id'},
        unique = false,
        if_not_exists = true,
    })

    test_space:create_index('name', {
        type = 'tree',
        parts = {'book_name'},
        unique = true,
        if_not_exists = true,
    })

    local customers = box.schema.space.create(
        'customers',
        {
            format = {
                -- {name = 'uuid', type = 'uuid'}, -- blocked by https://github.com/tarantool/crud/issues/84
                {name = 'id', type = 'unsigned'},
                {name = 'bucket_id', type = 'unsigned'},
                {name = 'name', type = 'string'},
                {name = 'tags', type = 'array'},
                {name = 'addresses', type = 'any'},
                {name = 'lastVisitTime', type = 'unsigned'},
            },
            if_not_exists = true,
        }
    )

    customers:create_index('id', {
        parts = {'id'},
        if_not_exists = true,
    })

    customers:create_index('bucket_id', {
        parts = {'bucket_id'},
        unique = false,
        if_not_exists = true,
    })
end

local function storage_get_space_format()
    local ddl = require('ddl')
    return ddl.get_schema()
end

local function find_books_by_name(names)
    local books = {}
    for _, name in pairs(names) do
        books[#books+1] = box.space.test_space.index.name:select(name)[1]
    end
    return books
end

local function init(opts)
    if opts.is_master then
        init_space()

        box.schema.func.create('storage_get_space_format', {if_not_exists = true})
    end

    rawset(_G, 'storage_get_space_format', storage_get_space_format)
    rawset(_G, 'find_books_by_name', find_books_by_name)

    return true
end

return {
    role_name = 'app.roles.api_storage',
    init = init,
    utils = {
        storage_get_space_format = storage_get_space_format,
        find_books_by_name = find_books_by_name,
    },
    dependencies = {
        'cartridge.roles.crud-storage'
    }
}
