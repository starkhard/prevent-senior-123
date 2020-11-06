[![Build Status](https://travis-ci.org/starkhard/prevent-senior-123.svg?branch=master)](https://travis-ci.org/starkhard/prevent-senior-123)


# Challenge-Prevent Senior
Prevent Senior Challenge, where it is necessary to build is an application to create log files FAST, through requests or LOT processing.

# Project requirements:
<p>[JDK 1.8] - Minimum version of Java JDK 1.8  </p>
<p>Docker </p>


# Open source resources

<p> Spring Boot </p>
<p> Spring batch</p>
<p> Rest API</p>
<p> Apache Solr </p>
<p> Lombok</p>
<p> Swagger</p>
<p> JPA </p>
<p> Thymeleaf</p>
<p> Jquery </p>
<p> BootStrap</p>

# Design Patterns used
<p> Builder </p>
<p> Populator </p>

# First Instruction

<p>
<h3>Create Jar in order to execute docker compose</h3>
 <pre> mvn clean install -DskipTests </pre>
 </p>
 
 
<h1>Alert : <h2>To perform this operation avoid being connected to any VPN, or external PROXY </h2></h1>
<p> Before carrying out the process, have the docker installed on the machine </p>
<p>In this path run the command  <b> docker compose  up -d  </b> It will install the following containers: </p>



<p> <b> Solr </b> </p>
<p> <b> Postgres SQL </b> </p>
<p> <b> pgAdmin </b> </p>
<p> <b> Redis </b> </p>



 
<pre>Creating postgres_container     ... <font color="#4E9A06">done</font>
Creating prevent-senior_redis_1 ... <font color="#4E9A06">done</font>
Creating solr_prevent           ... <font color="#4E9A06">done</font>
Creating pgadmin_container      ... <font color="#4E9A06">done</font></pre>

<p>This also will create  database default for test the project </p>

<p><b>Solr Access </b> ... http://localhost:8983/solr </p>
<p><b>Pg Admin</b>...  http://127.0.0.1:5050/browser/

User : admin@preventsenior.com
<br/>
Password : admin

</p>
<p><b>Postgres DataBase</b>... 
 
 docker inspect [CONTAINER_ID]  | grep IPAddress
 <pre> &quot;Secondary<font color="#EF2929"><b>IPAddress</b></font>es&quot;: null,
             &quot;<font color="#EF2929"><b>IPAddress</b></font>&quot;: &quot;&quot;,
                     &quot;<font color="#EF2929"><b>IPAddress</b></font>&quot;: &quot;[ IP HERE ] &quot;</pre>
                     
                     
Get this <b>IPAddress </b> to connect to Pg admin server.                     
 </p>


# Load Data
<p>We have created  two folders to process the files , when  you move a  new file there</p>
<p><b>/upload </b>  ... this folder that will watch new files , so  
how i'm using the watchService to detect new files on linux maybe is necessary you running the following command  case this no working for you : <br/>
 <br/>
 <b>
 sudo sysctl fs.inotify.max_user_watches=524288
 <br/>
 sudo sysctl -p
 </b>
 <br/>
 <br/>
 <br/>
 This folder /upload , will load all datas (access.logs)  
 </p>
 
 </br>
 </br>
 
 <p><b>/success</b> .. this folder will get the files processed in the execution case we have a success Status for the JOBS</p>
 
 
 # FRONT END SIDE 
 
 <p>Even though API's were built to perform the necessary tests for searching, inserting and etc. for logs, a simple front end was created with Spring Thymeleaf</p>
 
 <br/>
 <p><b>URL</b>... http://localhost:8080/upload</p>
 
