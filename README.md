# capability-data-json

Atomic authority package for `data/json`.

- imports: `#{:json-extract-field :json-encode}`
- effects: `#{:codec}`
- default policy: `:autonomous`
- provider status: `contract-only`

Importing this package does not grant runtime authority. Tamaki must
request it explicitly and Kototama must admit the sealed envelope.

```sh
clojure -M:test
```
