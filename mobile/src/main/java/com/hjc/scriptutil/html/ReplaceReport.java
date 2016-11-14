package com.hjc.scriptutil.html;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hujiachun on 16/9/9.
 */

public interface ReplaceReport {


    void replace(FileOutputStream fos, String rep) throws IOException;

    public class $start implements ReplaceReport{

        @Override
        public void replace(FileOutputStream fos, String rep) throws IOException {
            fos.write(rep.getBytes());
        }
    }

    public class $end implements ReplaceReport{

        @Override
        public void replace(FileOutputStream fos, String rep) throws IOException {
            fos.write(rep.getBytes());
        }
    }

    public class $time implements ReplaceReport{

        @Override
        public void replace(FileOutputStream fos, String rep) throws IOException {
            fos.write(rep.getBytes());
        }
    }

    public class $project implements ReplaceReport{

        @Override
        public void replace(FileOutputStream fos, String rep) throws IOException {
            fos.write(rep.getBytes());
        }
    }

    public class $device implements ReplaceReport{

        @Override
        public void replace(FileOutputStream fos, String rep) throws IOException {
            fos.write(rep.getBytes());
        }
    }

    public class $anr implements ReplaceReport{

        @Override
        public void replace(FileOutputStream fos, String rep) throws IOException {
            fos.write(rep.getBytes());
        }
    }

    public class $crash implements ReplaceReport{

        @Override
        public void replace(FileOutputStream fos, String rep) throws IOException {
            fos.write(rep.getBytes());
        }
    }
}


