local pool = require('cartridge.pool')
local errors = require('errors')
local cartridge_pool = require('cartridge.pool')
local cartridge_rpc = require('cartridge.rpc')

local AssertionError = errors.new_class('AssertionError')

local function get_schema()
    for _, instance_uri in pairs(cartridge_rpc.get_candidates('app.roles.api_storage', { leader_only = true })) do
        local conn = cartridge_pool.connect(instance_uri)
        return conn:call('ddl.get_schema', {})
    end
end

function find_by_complex_query(year)
    return require('fun')
            .iter(crud.pairs('test_space'))
            :filter(function(b)
                return b[6] and b[6] > year
            end)
            :totable()
end

function update_by_complex_query(id, year)
    return crud.update('test_space', id, { { '=', 'year', year } })
end

function update_by_complex_query_without_return(id, year)
    crud.update('test_space', id, { { '=', 'year', year } })
end

function batch_update_books(books)
    local result = {}
    local failures = {}
    for _, book in pairs(books) do
        local ok, obj, err = pcall(crud.replace_object, 'test_space', book)
        if ok and err == nil then
            result[#result + 1] = obj.rows[1]
        else
            failures[#failures + 1] = ok and tostring(err) or tostring(obj)
        end
    end
    if #failures > 0 then
        return nil, "Failed to update: " .. table.concat(failures, ', ')
    end
    return result
end

-- -- blocked by https://github.com/tarantool/crud/issues/106
--function find_customer_by_address(address)
--	return crud.select('customers', {{'=', "addresses.home.city", address.city}})
--end

function find_by_entity(book)
    return crud.select('test_space', { { '=', 'id', book.id } })
end

local function get_uriList()
    local uriLeader, err = cartridge.rpc_get_candidates('app.roles.api_storage', { leader_only = true })
    if err ~= nil then
        return nil, err
    end
    return uriLeader
end

function book_find_list_by_name(names)
    local books_list = {}
    local books_by_storage, err = pool.map_call('find_books_by_name', { names }, { uri_list = get_uriList() })
    if err then
        return nil, err
    end
    for _, books in pairs(books_by_storage) do
        for _, book in pairs(books) do
            table.insert(books_list, book)
        end
    end
    return books_list
end

function find_customer_by_address(address)
    local customer_list = {}
    local customers_by_storage, err = pool.map_call('find_customer_by_address', { address }, { uri_list = get_uriList() })
    if err then
        return nil, err
    end
    for _, customers in pairs(customers_by_storage) do
        for _, customer in pairs(customers) do
            table.insert(customer_list, customer)
        end
    end
    return customer_list
end

function find_customer_by_book(book)
    local customer_list = {}
    local customers_by_storage, err = pool.map_call('find_customer_by_book', { book }, { uri_list = get_uriList() })
    if err then
        return nil, err
    end
    for _, customers in pairs(customers_by_storage) do
        for _, customer in pairs(customers) do
            table.insert(customer_list, customer)
        end
    end
    return customer_list
end

function find_book_by_address(address)
    local book_list = {}
    local books_by_storage, err = pool.map_call('find_book_by_address', { address }, { uri_list = get_uriList() })
    if err then
        return nil, err
    end
    for _, books in pairs(books_by_storage) do
        for _, book in pairs(books) do
            table.insert(book_list, book)
        end
    end
    return book_list
end

function find_book_by_book(book)
    local book_list = {}
    local books_by_storage, err = pool.map_call('find_book_by_book', { book }, { uri_list = get_uriList() })
    if err then
        return nil, err
    end
    for _, books in pairs(books_by_storage) do
        for _, book in pairs(books) do
            table.insert(book_list, book)
        end
    end
    return book_list
end

function get_customer_addresses()
    return crud.pairs('customers')
               :map(function(c)
        return c[5]
    end)
               :reduce(function(acc, addresses)
        for _, a in pairs(addresses) do
            table.insert(acc, a)
        end
        return acc
    end, {})
end

function returning_error()
    return nil, AssertionError:new('some error')
end

function get_users_with_age_gt(age)
    return crud.select("sample_user", { { ">", "age", age } })
end

function get_predefined_user()
    return { name = "John", age = 46 }
end

function get_age_by_name(name)
    local user, err = crud.get("sample_user", name)
    if err ~= nil then
        return user, err
    end
    if user.rows[1] ~= nil then
        return user.rows[1][2]
    end
    return nil
end

function returning_nothing()
end

function returning_number()
    return 1
end

function returning_string()
    return "test string"
end

function returning_simple_array()
    return { { nil, true, "abc", 123, 1.23 } }
end

function returning_simple_arrays()
    return { { nil, true, "abc", 123, 1.23 }, { 1, false, "cba", 321, 3.21 } }
end

function returning_crud_response_one_tuple()
    return { metadata = {}, rows = { { nil, true, "abc", 123, 1.23 } } }
end

function returning_crud_response_two_tuples()
    return { metadata = {}, rows = { { nil, true, "abc", 123, 1.23 }, { 1, false, "cba", 321, 3.21 } } }
end

function returning_simple_map()
    return { testId = nil, testBoolean = true, testString = "abc", testInteger = 123, testDouble = 1.23 }
end

function returning_simple_maps()
    return {
        { testId = nil, testBoolean = true, testString = "abc", testInteger = 123, testDouble = 1.23 },
        { testId = 1, testBoolean = false, testString = "cba", testInteger = 321, testDouble = 3.21 },
    }
end

function returning_object()
    return { test = "testString", testNumber = 4 }
end

function returning_object_list()
    return { { test = "testString", testNumber = 4 }, { test = "testString2", testNumber = 10 } }
end

function returning_nil()
    return nil
end

function insert_book_with_custom_type(book_id, issue_date)
    return crud.insert('test_space', { book_id, nil, 'ghj556', 'Hitchicker\'s Guide to the Galaxy', 'Douglas Adams', 1981, nil, nil, nil, issue_date })
end

function save_book(book)
    return crud.insert_object('test_space', book)
end

function find_book_by_id(book_id)
    return crud.select('test_space', { { '=', 'id', book_id } })
end

local function create_restricted_user()
    box.schema.func.create("returning_simple_map", { if_not_exists = true, setuid = true })
    box.schema.func.create("returning_simple_maps", { if_not_exists = true, setuid = true })
    box.schema.func.create("returning_simple_array", { if_not_exists = true, setuid = true })
    box.schema.func.create("returning_simple_arrays", { if_not_exists = true, setuid = true })

    box.schema.user.create('restricted_user', { if_not_exists = true, password = 'restricted_secret' })
    box.schema.user.grant("restricted_user", "execute", "function", "returning_simple_map", { if_not_exists = true })
    box.schema.user.grant("restricted_user", "execute", "function", "returning_simple_maps", { if_not_exists = true })
    box.schema.user.grant("restricted_user", "execute", "function", "returning_simple_array", { if_not_exists = true })
    box.schema.user.grant("restricted_user", "execute", "function", "returning_simple_arrays", { if_not_exists = true })
end

local function init(opts)
    if opts.is_master then
    end

    create_restricted_user()
    rawset(_G, 'ddl', { get_schema = get_schema })

    return true
end

return {
    role_name = 'app.roles.api_router',
    init = init,
    dependencies = {
        'cartridge.roles.crud-router'
    }
}
