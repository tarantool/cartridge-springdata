local vshard = require('vshard')
local pool = require('cartridge.pool')
local errors = require('errors')

local AssertionError = errors.new_class('AssertionError')

-- function to get cluster schema
local function crud_get_schema()
	local replicaset = select(2, next(vshard.router.routeall()))
	local uniq_spaces = {}
	local spaces_ids = {}
	for _, space in pairs(replicaset.master.conn.space) do

		if (spaces_ids[space.id] == nil) then
			local space_copy = {
				engine = space.engine,
				field_count = space.field_count,
				id = space.id,
				name = space.name,
				indexes = {},
				format = space._format,
			}

			for i, space_index in pairs(space.index) do
				if type(i) == 'number' then
					local index_copy = {
						id = space_index.id,
						name = space_index.name,
						unique = space_index.unique,
						type = space_index.type,
						parts = space_index.parts,
					}
					table.insert(space_copy.indexes, index_copy)
				end
			end

			table.insert(uniq_spaces, {space_copy} )
			spaces_ids[space.id] = true
		end
	end
	return uniq_spaces
end

function find_by_complex_query(year)
	return require('fun').iter(crud.pairs('test_space'))
			:filter(function(b) return b[6] and b[6] > year end):totable()
end

function update_by_complex_query(id, year)
	return crud.update('test_space', id, {{'=', 'year', year}})
end

function find_customer_by_address(address)
	return crud.select('customers', {{'=', "addresses.home.city", address.city}})
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

local function init(opts)
    if opts.is_master then
    end

    rawset(_G, 'crud_get_schema', crud_get_schema)

    return true
end

return {
    role_name = 'app.roles.api_router',
    init = init,
    dependencies = {
        'cartridge.roles.crud-router'
    }
}
