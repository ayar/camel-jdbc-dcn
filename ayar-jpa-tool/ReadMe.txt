DOCKER
DOwnload  a linux image from http://www.oracle.com/technetwork/database/database-technologies/express-edition/downloads/index.html

8. Run the appropriate command to build your image. Use --no-cache=true if you
   do not want to cache intermediate images.
    cd ~/database
    docker build -t oracle/db/xe:11.2.0.1.0 --shm-size=2g \
    -f ~/database/xe/Dockerfile.11.2.0.1.0 .

9. To start a container running the database, run the following command:
    docker run -i -P --shm-size=2g --name=xe-db1 -t oracle/db/xe:11.2.0.1.0