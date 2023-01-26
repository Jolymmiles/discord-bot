package com.jesys.notify.daemons;

import java.io.IOException;

public interface Daemon {
    void checkNewPosts() throws IOException;
}
