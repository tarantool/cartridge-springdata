local vshard = require('vshard')
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
	return require('fun').iter(crud.pairs('test_space'))
			:filter(function(b) return b[6] and b[6] > year end):totable()
end

function update_by_complex_query(id, year)
	return crud.update('test_space', id, {{'=', 'year', year}})
end

-- -- blocked by https://github.com/tarantool/crud/issues/106
--function find_customer_by_address(address)
--	return crud.select('customers', {{'=', "addresses.home.city", address.city}})
--end

function find_by_entity(book)
	return crud.select('test_space', {{'=', 'id', book.id}})
end

local function get_uriList()
	local uriLider, err = cartridge.rpc_get_candidates('app.roles.api_storage', {leader_only=true})
	if err ~= nil then
		return nil, err
	end
	return uriLider
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

local function init(opts)
    if opts.is_master then
    end

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
