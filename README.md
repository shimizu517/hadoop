# Set up

## Only first time

```bash
wget https://dlcdn.apache.org/hadoop/common/stable/hadoop-3.3.6.tar.gz  # any version that you prefer.
tar -xf hadoop-3.3.6.tar.gz hadoop-3.3.6/ && rm hadoop-3.3.6.tar.gz
```

## Start hadoop

```bash
dc up -d
```

## Port forwarding(local)

```bash
# port forwarding
~/tools/dev_port.sh 9870 8088
```

## Execute WordCount hadoop map reducer

```bash
dc run --rm wordcount bash
cd /app/src
javac -classpath $(/opt/hadoop/bin/hadoop classpath) WordCount.java  # compile
jar -cvf WordCount.jar WordCount*.class
/opt/hadoop/bin/hadoop jar WordCount.jar WordCount input.txt output
```

# Tips
## REPL
```bash
jshell
```

# Trouble shooting

## When datanode fails because the namenode and datanode cluster ID does not match.

```bash
docker-compose down && docker volume ls && docker volume prune
```

# Note

## How whole hadoop map reduce process works in a nutshell.(https://data-flair.training/blogs/how-hadoop-mapreduce-works/)
1. "InputFormat" creates "InputSplits" from input data.
  - Splits the input data into chunks(=InputSplits). e.g., text file into lines.
2. "RecordReader" converts InputSplit into key-value pair.
  - Reads the data chunks and converts them into key-value pairs. e.g., a line into {line number: "Hello this is a line. Hello again."}
3. "Mapper" processes the key-value pair and generates intermediate key-value pairs.
  - Processes the key-value pairs and generates intermediate key-value pairs. e.g., When receiving {line number: "Hello this is a line. Hello again."}, it might generate {"Hello":1}, {"Hello":1}, {"this":1}, {"is":1}, {"a":1}, {"line":1}, {"again":1} for each word.
4. "Combiner" receives the intermediate key-value pairs and reduces a bit (With vs without Combiner: https://data-flair.training/blogs/hadoop-combiner-tutorial/)
  - It reduces the intermediate key-value pairs. e.g., {"Hello":1}, {"Hello":1} -> {"Hello":(1,1)}
5. "Partitioner" groups the intermediate key-value pairs from combiner by hash(key)
  - The purpose is to send the same key-value pairs to the same reducer.
    - So it is used when there are multiple reducers.
    - Multiple reducers seems to be used when the data is too large to be processed by a single reducer.
6. "Shuffling and Sorting" sends the intermediate key-value pairs to the reducer.
  - Done by Hadoop's internal components.
  - Network communication.
7. "Reducer" receives the intermediate key-value pairs and generates final key-value pairs.
  - Processes the intermediate key-value pairs and generates final key-value pairs. e.g., {"Hello":(1,1)} -> {"Hello":2}
8. "RecordWriter" writes the final key-value pairs to the output file based on "OutputFormat".
  - Writes the final key-value pairs to the output file. e.g., {"Hello":2} -> "Hello:2"

# References

- https://www.youtube.com/watch?v=gEm4XY04WAU (Hadoop MapReduce Tutorial | MapReduce Tutorial For Beginners | Hadoop Training | Edureka)
- https://data-flair.training/blogs/how-hadoop-mapreduce-works/ (How Hadoop MapReduce Works?)
- https://0x0fff.com/hadoop-mapreduce-comprehensive-description/ (Explains the whole hadoop map reduce process)
