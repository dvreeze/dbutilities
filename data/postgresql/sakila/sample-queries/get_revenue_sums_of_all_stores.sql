select payment_date, amount, sum(amount) OVER (order by payment_date)
  from (
  select cast(payment_date as date) as payment_date, sum(amount) as amount
    from payment
   group by cast(payment_date as date)
) p
 order by payment_date
