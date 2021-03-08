-- fill test space

local crud = require('crud')

crud.insert('test_space', {1, nil, 'a1', 'Don Quixote', 'Miguel de Cervantes', 1605})
crud.insert('test_space', {2, nil, 'a2', 'The Great Gatsby', 'F. Scott Fitzgerald', 1925})
crud.insert('test_space', {3, nil, 'a3', 'War and Peace', 'Leo Tolstoy', 1869})
