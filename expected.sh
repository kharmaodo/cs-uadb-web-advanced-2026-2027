head -c 300 contrats.json
python3 -m json.tool contrats.json > /dev/null && echo "JSON OK"