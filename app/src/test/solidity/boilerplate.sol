pragma solidity ^0.8.0;
    // NPM dependencies
    import "@openzeppelin/contracts/token/ERC721/extensions/ERC721Enumerable.sol";
    import '@openzeppelin/contracts/access/Ownable.sol';

    contract Boilerplate is Ownable, ERC721Enumerable {
        using Strings for uint256;
        
        // Optional mapping for token URIs
        mapping (uint256 => string) private _tokenURIs;

        // Base URI
        string private _baseURIextended;


        constructor(string memory _name, string memory _symbol)
            ERC721(_name, _symbol)
        {}
        
        function setBaseURI(string memory baseURI_) external onlyOwner() {
            _baseURIextended = baseURI_;
        }
        
        function _setTokenURI(uint256 tokenId, string memory _tokenURI) internal virtual {
            require(_exists(tokenId), "You are setting the URI for a non-existent token");
            _tokenURIs[tokenId] = _tokenURI;
        }
        
        function _baseURI() internal view virtual override returns (string memory) {
            return _baseURIextended;
        }

        // The code takes in the tokenID and returns the relevant tokenURI.
        function tokenURI(uint256 tokenId) public view virtual override returns (string memory) {
            require(_exists(tokenId), "You are requesting the URI for a non-existent token");

            string memory _tokenURI = _tokenURIs[tokenId];
            string memory base = _baseURI();
            
            if (bytes(base).length == 0) {
                return _tokenURI;
            }
            if (bytes(_tokenURI).length > 0) {
                return string(abi.encodePacked(base, _tokenURI));
            }
            return string(abi.encodePacked(base, tokenId.toString()));
        }
    }