Короткий опис:

Створюється ConcurrentHashMap, ключем якого є місце, а значенням обєкт типу (чи вільне місце, ціна місця)

Створюється availableSeatsTask для отримання списку місці, які не зайнятті.

Створюється optimalSeatTask завдяки методу thenCompose(), який отримує вільні місця з availableSeatsTask і на основі цього шукає місце з найнижчою ціною.

С допомогою join() отримується результат, при умові що результат не порожні, бронюється білет та виводиться місце у консоль, інакше друкується повідомлення про те, що не було вільних місць.