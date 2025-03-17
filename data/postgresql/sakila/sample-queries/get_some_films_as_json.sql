select json_object(
           'film': json_object(
               'film_id': film.film_id,
               'title': film.title,
               'description': film.description,
               'release_year': film.release_year,
               'language_id': film.language_id,
               'rental_duration': film.rental_duration,
               'rental_rate': film.rental_rate,
               'length': film.length,
               'replacement_cost': film.replacement_cost,
               'rating': film.rating,
               'last_update': film.last_update,
               'special_features': array_to_string(film.special_features, ', '),
               'fulltext': film.fulltext,
               'actors': json_agg(
                   json_object('actor': json_object(
                           'actor_id': film_actor.actor_id,
                           'film_actor_last_update': film_actor.last_update,
                           'actor_first_name': actor.first_name,
                           'actor_last_name': actor.last_name,
                           'actor_last_update': actor.last_update
                       )
                   )
               )
           )
       )
  from film
  left join film_actor on (film.film_id = film_actor.film_id)
  left join actor on (film_actor.actor_id = actor.actor_id)
 where upper(film.title) like ?
 group by film.film_id
