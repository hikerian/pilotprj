set OLLAMA_HOST=0.0.0.0:11434

echo %OLLAMA_HOST%

ollama pull gemma4:e2b

ollama run gemma4:e2b