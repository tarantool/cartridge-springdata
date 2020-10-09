-- create test space
s = box.schema.space.create('test_space')
s:format({
    {name = 'id', type = 'unsigned'},
    {name = 'unique_key', type = 'string'},
    {name = 'book_name', type = 'string'},
    {name = 'author', type = 'string'},
    {name = 'year', type = 'unsigned',is_nullable=true},
    {name = 'test_delete', type = 'unsigned',is_nullable=true},
    {name = 'test_delete_2', type = 'unsigned',is_nullable=true},
    {name = 'number_field', type = 'number', is_nullable=true}
})
s:create_index('primary', {
    type = 'tree',
    parts = {'id'}
})
s:create_index('inx_author', {
    type = 'tree',
    unique = false,
    parts = {'author'}
})
s:create_index('secondary', {
    type = 'hash',
    unique = true,
    parts = {'unique_key'}
})

s:insert{1, 'a1', 'Don Quixote', 'Miguel de Cervantes', 1605}
s:insert{2, 'a2', 'The Great Gatsby', 'F. Scott Fitzgerald', 1925}
s:insert{3, 'a3', 'War and Peace', 'Leo Tolstoy', 1869}

function user_function_no_param()
    return 5;
end

function user_function_two_param(a, b)
    return a, b, 'Hello, '..a..' '..b;
end

function find_by_complex_query(year)
    return s:pairs():filter(function(b) return b.year > year end):totable()
end
