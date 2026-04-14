@echo off
cd /d C:\Users\byy90\.qclaw\workspace\personal-blog\server
C:\Users\byy90\scoop\apps\maven\current\bin\mvn.cmd compile exec:java -Dexec.mainClass="com.blog.util.Gen" -q
