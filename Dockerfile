FROM clojure:lein

COPY . /usr/src/app

WORKDIR /usr/src/app

EXPOSE 3000

CMD ["lein", "run"]
