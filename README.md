# java-explore-with-me

Стек: Java 11, Spring Boot, Spring Data JPA, PostgreSQL, Docker, Maven

Свободное время — ценный ресурс. Ежедневно мы планируем, как его потратить — куда и с кем сходить. Сложнее всего в таком планировании поиск информации и переговоры. Нужно учесть много деталей: какие намечаются мероприятия, свободны ли в этот момент друзья, как всех пригласить и где собраться. Explore With Me — афиша. В этой афише можно предложить какое-либо событие от выставки до похода в кино и собрать компанию для участия в нём.

Микросервисная архитектура
Приложение состоит из 2 сервисов:

stats-service - часть приложения, которая собирает, хранит и отдает по запросу статистику по просмотрам.
main-service - основная часть приложения, в которой происходит вся логика приложения.
