package ru.rustyskies.utils;

/**
 * @author Egor Markin
 * @since 29.08.2012
 */
public class OsUtils {

	// Based on http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
	
	public static void main(String[] args) {
		if (isWindows()) {
			System.out.println("This is Windows");
		} else if (isMac()) {
			System.out.println("This is Mac");
		} else if (isUnix()) {
			System.out.println("This is Unix or Linux");
		} else if (isSolaris()) {
			System.out.println("This is Solaris");
		} else {
			System.out.println("Your OS is not support!!");
		}
	}
	
	public static String getOsName() {
		return System.getProperty("os.name");
	}
 
	public static boolean isWindows() {
		return (System.getProperty("os.name").toLowerCase().contains("win"));
	}
 
	public static boolean isMac() {
		return (System.getProperty("os.name").toLowerCase().contains("mac"));
	}
 
	public static boolean isUnix() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.contains("nix") || os.contains("nux")); // linux or unix
	}
 
	public static boolean isSolaris() {
		return (System.getProperty("os.name").toLowerCase().contains("sunos"));
	}	
}
