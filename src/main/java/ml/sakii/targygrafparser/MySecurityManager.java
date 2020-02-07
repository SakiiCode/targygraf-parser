package ml.sakii.targygrafparser;

import java.security.Permission;

class MySecurityManager extends SecurityManager {
	  @Override public void checkExit(int status) {
	    throw new SecurityException();
	  }

	  @Override public void checkPermission(Permission perm) {
		  
	  }
	}