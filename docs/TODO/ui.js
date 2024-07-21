
// FRONT-END REACT
<ui.Chart service="product/chart" labels={{
    series: {
        name: 'Nome',
        quantity: 'Quantidade'
    },
    axis: [ 'Categoria' ]
}} />

// BACK-END SERVICE
_ui.chart({
    type: 'line',
    series: [ 'name', 'quantity' ],
    axis: [ 'category' ],
    from: 'product inner join category on ...'
})





// FRONT-END REACT
<ui.Table service="product/list" />

// BACK-END SERVICE   HEAD product/list?info
_ui.table({
    columns: [ 'name', 'category' ],
    from: 'product inner join category on ...',
    filters: [
        { table: 'product', column: 'name' }
    ],
    page: 10,
})

// FRONT-END REACT
<ui.Form service="product" />

// BACK-END SERVICE
_ui.form({
    name: 'product',
    update: true,
    fields: [
        _ui.form.field.text({
            name: 'name',
            required: true
        }),
        _ui.form.field.number({
            name: 'quantity',
            required: true,
            max: 100,
            min: 1
        })
    ]
})
