import json

try:
    with open(r'c:\Projects\SearchEngineSimulator\backend\src\main\resources\topics_seed.json', 'r', encoding='utf-8') as f1:
        data1 = json.load(f1)
        
    with open(r'c:\Projects\SearchEngineSimulator\backend\src\main\resources\topics_seed_2.json', 'r', encoding='utf-8') as f2:
        data2 = json.load(f2)
        
    merged = data1 + data2
    
    with open(r'c:\Projects\SearchEngineSimulator\backend\src\main\resources\topics_seed.json', 'w', encoding='utf-8') as f:
        json.dump(merged, f, indent=2)
        
    print(f"Successfully merged. Total topics: {len(merged)}")
except Exception as e:
    print(f"Error: {e}")
