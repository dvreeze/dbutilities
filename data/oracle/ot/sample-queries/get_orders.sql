select ord.order_id, ord.customer_id, ord.status, ord.salesman_id, ord.order_date,
       itm.item_id, itm.product_id, itm.quantity, itm.unit_price,
       prod.product_name, prod.description, prod.standard_cost, prod.list_price, prod.category_id,
       cat.category_name
  from orders ord
  left join order_items itm on (ord.order_id = itm.order_id)
  left join products prod on (itm.product_id = prod.product_id)
  left join product_categories cat on (prod.category_id = cat.category_id)
