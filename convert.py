import json
import yaml

with open('contrats.yaml', 'r', encoding='utf-8') as yaml_file:
    data = yaml.safe_load(yaml_file)

with open('contrats.json', 'w', encoding='utf-8') as json_file:
    json.dump(data, json_file, indent=2, ensure_ascii=False)
