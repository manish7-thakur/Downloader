# Downloader
This application downloads data from multiple locations using different protocols

## Command Line Testing

### Download a file

#### HTTP

    curl -H "Content-Type:application/json; charset=utf-8" \
         -v \
         -X POST \
         -d '{"url": "http://www.pdf995.com/samples/widgets.pdf", "location": "/Users/mthakur/Downloads"}' \
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
