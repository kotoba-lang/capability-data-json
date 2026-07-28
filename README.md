# capability-data-json

Atomic authority package for `data/json`.

- imports: `#{:json-extract-field :json-encode}`
- effects: `#{:codec}`
- default policy: `:autonomous`
- semantic definition CID: `bafyreia2fucycy5w6nmxp3j473pz5vzamg2yisjiog6jw7bzoeqzqcprfq`
- hash contract CID: `bafkreiflhj3fslsbh7okdas2fzlhmogai64x6p3lkla6gtr7berbp7ftvi`
- provider status: `contract-only`

The repository name is a discovery alias. The semantic definition CID
is the immutable import identity. Importing it does not grant runtime
authority: Tamaki must request it explicitly and Kototama must admit
the sealed envelope.

```sh
clojure -M:test
```
