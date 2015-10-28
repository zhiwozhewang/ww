package org.geometerplus.zlibrary.core.util;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamHolder {
	InputStream getInputStream() throws IOException;
}
