pragma solidity ^0.8.0;
//view keyword gives return value
import "@openzeppelin/contracts/utils/Counters.sol";
import "./example.sol";

 contract ExampleToken is Example {
     using Counters for Counters.Counter;
     Counters.Counter _tokIds;
     ERC721Enumerable Erc721enumerable; 
     
     string tokenName = "";
     string tokenSymbol = "";
     uint256 tokenID1 = 0;

    
    constructor() Example(tokenName, tokenSymbol) public {

     }

    function mintUniqueToken(address _to, string memory _tokenURI)  public returns (uint256) {

      _tokIds.increment();
      safeMint(_to,  _tokIds.current());
      _setTokenURI( _tokIds.current(), _tokenURI);
        
      return newTokenId;        
     }

     function ap(address op) public{
        setApprovalForAll(op, true);
     }

     function tr(address from, address _to, uint256 tokenId) public {
         _transfer(ownerOf(tokenId), _to, tokenId);
     }

     function getTokenIds(address tokOwner) public view returns (uint[] memory) {

        uint[] memory ownerTokens = new uint[](ERC721.balanceOf(tokOwner));

         // mint tokens to contract address, then call this function on contract owner address
        for (uint i=0;i<ERC721.balanceOf(tokOwner);i++){
            ownerTokens[i] = super.tokenOfOwnerByIndex(tokOwner, i); // 
        }
        return (ownerTokens);
    }
 }