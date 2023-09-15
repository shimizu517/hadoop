## Only first time
```bash
wget https://dlcdn.apache.org/hadoop/common/stable/hadoop-3.3.6.tar.gz  # any version that you prefer.
tar -xf hadoop-3.3.6.tar.gz hadoop-3.3.6/ && rm hadoop-3.3.6.tar.gz
```

## Start hadoop
```bash
dc up -d
```

## Start hadoop map reducer
```bash
dc run --rm wordcount bash
cd /app/src
javac WordCount.java -cp $(/opt/hadoop/bin/hadoop classpath)  # compile
java WordCount
```

## Port forwarding(local)
```bash
# port forwarding
~/tools/dev_port.sh 9870 8088
```

# Trouble shooting

## When datanode fails because the namenode and datanode cluster ID does not match.
```bash
docker-compose down && docker volume ls && docker volume prune
```

# References
- https://www.youtube.com/watch?v=gEm4XY04WAU
