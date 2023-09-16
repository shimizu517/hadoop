import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class WordCount {
  public static class Map extends Mapper<LongWritable, Text, Text, IntWritable> {
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      // String is built-in in Java class.
      String line = value.toString();
      // StringTokenizer is a class that splits a string into tokens by delimiter(whitespace in this case).
      StringTokenizer t = new StringTokenizer(line);

      while (t.hasMoreTokens()) {
        // Allegedly, value.set is used instead of new Text for the sake of performance.
        value.set(t.nextToken());

        // *context.write
        // The first param is the key, the second is the value.
        // In this program where we are counting words, the key is the word itself and the value is 1 which will be
        // used to count the number of occurrences of a word by reducer.
        // e.g., value = "hello", context.write(value, new IntWritable(1)) will be written as "hello 1"
        context.write(value, new IntWritable(1));
      }
    }
  }
  public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable> {
    /**
     * @param key     is same as the first param of context.write in map function, which was "value".
     * @param values  is an iterable of values that was passed to context.write in map function as its second param with the same key.
     */
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable x: values) {
        sum += x.get();
      }
      // *context.write
      // key is the word, value is the number of occurrences of the word.
      // In our case, the key is equal to the first param of context.write in map function, which was "value".
      // e.g., key = "hello", context.write(key, new IntWritable(sum)) will be written as "hello 2"
      context.write(key, new IntWritable(sum));
    }
  }
  

  public static void main(String[] args) throws Exception {
    System.out.println("Starting WordCount program!");
    Configuration conf = new Configuration();

    Job job = Job.getInstance(conf, "WordCount");

    // set the class of the main function.
    job.setJarByClass(WordCount.class);
    // set the class of the mapper.
    job.setMapperClass(Map.class);
    // set the class of the reducer.
    job.setReducerClass(Reduce.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);

    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    Path outputPath = new Path(args[1]);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    outputPath.getFileSystem(conf).delete(outputPath, true);

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
