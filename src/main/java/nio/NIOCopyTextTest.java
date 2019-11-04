package nio;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Create by Gethin Wang on 2019/10/29
 */

public class NIOCopyTextTest {

    public static void main(String args[]) {
        try {
            RandomAccessFile aFile = new RandomAccessFile("C:\\Users\\10007886\\Desktop\\nio.txt", "rw");
            FileChannel channel = aFile.getChannel();
            RandomAccessFile bFile = new RandomAccessFile("C:\\Users\\10007886\\Desktop\\nio2.txt", "rw");
            FileChannel channel2 = bFile.getChannel();

            long position = 0;
            long count = channel.size();
//            channel2.transferFrom(channel,position,count);
            channel.transferTo(position,count,channel2);

        }catch (Exception e){

        }
    }
}


