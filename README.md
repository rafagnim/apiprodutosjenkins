<h1> Testes com Jenkins e Testcontainers </h1>

<hr>
<hr>

Projeto desenvolvido em conjunto com Allan Victor de Menezes Santos.

<hr>

Esta API compõe um dos itens do repositório: https://github.com/rafagnim/kotlintreinamento e contém as configurações necessárias para utilizar o Jenkins em conjunto com o Docker para a realização de testes, com o docker rodando no WSL2.

<hr>


Alguns comandos necessários - EXEMPLOS - (docker, etc):

Rabbit:
docker run --rm -it -p 15672:15672 -p 5672:5672 rabbitmq:3-management

http://localhost:15672/

Redis:
docker run --name my-redis -it -p 6379:6379 -d redis
docker exec -it CONTAINER redis-cli

Docker compose:
Exemplo:
docker-compose -f ARQUIVO.yaml up -d

<hr>

Há uma incompatibilidade entre o Docker rodando no WSL2 e a dependência TestContainers, mas no repositório a seguir é possível encontrar as configurações necessárias para utilizar o WSL2 + Docker + a dependência de Test Containers + Jenkins:

https://gist.github.com/sz763/3b0a5909a03bf2c9c5a057d032bd98b7

Segue uma transcrição resumida:

Criar no Linux: daemon.json file in /etc/docker, com o seguinte conteúdo:
{"hosts": ["tcp://0.0.0.0:2375", "unix:///var/run/docker.sock"]}

Reinicializar o docker daemon:
sudo systemctl daemon-reload
sudo systemctl restart docker ou sudo service docker restart

ou

wsl --shutdown (reabrindo novamente)

Verificar no LINUX se a porta exposta é a 2375:
netstat -nl | grep 2375

Ajustar as variáveis de ambiente no Windows:
Nome	Valor
DOCKER_HOST	tcp://localhost:2375
DOCKER_TLS_VERIFY	0
DOCKER_CERT_PATH	\\wsl$\home\$USER_NAME\.docker

Obs.: pode ser necessário substituir localhost pelo IP obtido no LINUX (ifconfig eth0)
Obs.: infelizmente, neste caso, a cada vez que reiniciar o docker, será necessário alterar esta variável para o IP gerado (ou então buscar uma alternativa para "trava" o IP no LINUX)
Obs.: $USER_NAME = usuário do LINUX
Obs.: Se a IDE estiver aberta, reiniciar novamente para que utilize as variáveis de ambiente alteradas (não há necessidade de reiniciar o Windows)
<hr>

Finalmente, para verificar no Windows se a porta 2375 está exposta, basta digitar no Prompt de Comando o seguinte:
netstat -o -n –a