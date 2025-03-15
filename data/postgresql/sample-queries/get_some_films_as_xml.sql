select xmlelement(name film,
           xmlelement(name film_id, film.film_id),
           xmlelement(name title, film.title),
           xmlelement(name description, film.description),
           xmlelement(name release_year, film.release_year),
           xmlelement(name language_id, film.language_id),
           xmlelement(name rental_duration, film.rental_duration),
           xmlelement(name rental_rate, film.rental_rate),
           xmlelement(name length, film.length),
           xmlelement(name replacement_cost, film.replacement_cost),
           xmlelement(name rating, film.rating),
           xmlelement(name last_update, film.last_update),
           xmlelement(name special_features, array_to_string(film.special_features, ', ')),
           xmlelement(name fulltext, film.fulltext),
           xmlelement(name actors,
               xmlagg(
                   xmlelement(name actor,
                       xmlforest(
                           film_actor.actor_id as actor_id,
                           film_actor.last_update as film_actor_last_update,
                           actor.first_name as actor_first_name,
                           actor.last_name as actor_last_name,
                           actor.last_update as actor_last_update
                       )
                   )
               )
           )
       )
  from film
  left join film_actor on (film.film_id = film_actor.film_id)
  left join actor on (film_actor.actor_id = actor.actor_id)
 where upper(film.title) like 'WA%'
 group by film.film_id
