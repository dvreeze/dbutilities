select first_name, last_name, count(*) films
  from actor as a
  join film_actor as fa using (actor_id)
 group by actor_id, first_name, last_name
 order by films desc
 limit 1
