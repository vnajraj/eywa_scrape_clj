# scraper

A _partial (missing json-rpc part)_ rewrite of dhmz2EYWA scraper in clojure.

More info on [dhmz2EYWA scraper](https://github.com/vnajraj/eywa_scrape).

## usage

1) Set environment variables e.g.:

```
cat > .env <<'EOF'
export EYWA_HOST="http://IP:PORT"
export EYWA_USERNAME="..."
export EYWA_PASSWORD="..."
EOF
```

```
source .env
```

2) Run

```
lein run
```

## dependencies

- "enlive" - to scrape the data
- "clj-http" - to authenticate and load data into eywa
- "org.clojure/data.json" - for json encoding
