
local crud = require('crud')

crud.init_storage()

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

end

local function storage_get_space_format()
    local ddl = require('ddl')
    return ddl.get_schema()
end

local function init(opts)
    if opts.is_master then
        init_space()

        box.schema.func.create('storage_get_space_format', {if_not_exists = true})
    end

    rawset(_G, 'storage_get_space_format', storage_get_space_format)

    return true
end

return {
    role_name = 'app.roles.api_storage',
    init = init,
    utils = {
        storage_get_space_format = storage_get_space_format,
    },
    dependencies = {
        'cartridge.roles.crud-storage'
    }
}
