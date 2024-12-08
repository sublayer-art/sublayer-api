Sublayer API Service
================
This is web service for sublayer.art, if runnling locally, API doc url could be visited at:
http://localhost:8082/doc.html

Upload NFT Metadata
---------------
### Overview
Before creating a NFT collection, an artist need to upload the NFT metadata first.
### Endpoint
https://sublayer.art/api/storage/upload
### Request Methods
POST
### Header
**Sublayer-Nft-Token:** User token 
### Request Body
multipart/form-data
### Request Parameters
**file**: binary file for NFT metadata (image, video or model files)
### Response Parameters
**id**: storeage id for the upload resource

**name**: filename

**type**: file type

**size**: file size

**url**: url path for the upload resource

**ipfshash**: ipfs path the upload resource


Create NFT Collection
---------------
### Overview
An artist creates the NFT collection.
### Endpoint
https://sublayer.art/api/contract/create
### Request Methods
POST
### Header
**Sublayer-Nft-Token:** User token 
### Request Body
application/x-www-form-urlencoded
### Request Parameters
**name**: name of the NFT collection

**symbol**: symbol of the NFT collection

**address**: contract address  of the NFT collection

**cover**: cover picture url of the NFT collection

**coverIpfs**: cover picture ipfs address of the NFT collection

**storageId**: storage id for picture

**owner**: owner of the collection

**signer**: signer of the collection