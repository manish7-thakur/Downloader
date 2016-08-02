# Downloader
This application can download data using different protocols. The server is built using SPRAY on top of AKKA with SCALA as the base language.
The application uses actor per request model for concurrency. Some of the unit test are kind of integrated test which requires an internet 
connection and local server setup. 

## Command Line Testing

### Download a file

#### HTTP

    curl -H "Content-Type:application/json; charset=utf-8" \
         -v \
         -X POST \
         -d '{"url": "http://www.pdf995.com/samples/widgets.pdf", "location": "/Users/mthakur/Downloads"}' \
         http://localhost:5000/api/download
         
#### HTTPS

    curl -H "Content-Type:application/json; charset=utf-8" \
         -v \
         -X POST \
         -d '{"url": "https://www.google.com", "location": "/Users/mthakur/Downloads"}' \
         http://localhost:5000/api/download
         
#### FTP

     curl -H "Content-Type:application/json; charset=utf-8" \
          -v \
          -X POST \
          -d '{"url": "ftp://ftp.funet.fi/pub/standards/RFC/rfc959.txt", "location": "/Users/mthakur/Downloads"}' \
          http://localhost:5000/api/download
          
#### SFTP

For SFTP protocol the server uses authentication. Port is assumed to be 22. The format for the URL would be :
`sftp://username:password@host;filePath`


     curl -H "Content-Type:application/json; charset=utf-8" \
          -v \
          -X POST \
          -d '{"url": "sftp://mthakur:l2dwq#y4b49@192.168.1.17;/etc/hosts", "location": "/Users/mthakur/Downloads"}' \
          http://localhost:5000/api/download
