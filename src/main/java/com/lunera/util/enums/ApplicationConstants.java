package com.lunera.util.enums;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ApplicationConstants {
	public static final int MAX_DUPLICATE_MSG_INTERVAL_MINUTES = 5;
	public static final Path DEFAULT_PROPERTIES_LOCATION = Paths.get(File.separatorChar + "lunera", "etc",
			"lunera.properties");
	public static final String ENVIRONMENT_VARIABLE = "LUN_CONFIG";
	public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
}
