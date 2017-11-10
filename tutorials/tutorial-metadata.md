# Fetching Metadata

To fetch assets from EMP backend one can call **getAssets** method and pass an implementation of **IMetadataCallback<ArrayList<EmpAsset>>** as an argument.

```java
EMPMetadataProvider.getInstance().getAssets("/content/asset?fieldSet=ALL&&includeUserData=true&pageNumber=1&sort=originalTitle&pageSize=100&onlyPublished=true&assetType=CLIP", new IMetadataCallback<ArrayList<EmpAsset>>() {
	@Override
    public void onMetadata(ArrayList<EmpAsset> metadata) {
        // Do things with the assets, for instance show them in an view via an adapter
    }

    @Override
    public void onError(ExposureError error) {
        // Handle error
    }
);
```

Equivalent methods exist for the following purposes:
- Fetching series & episodes
- Fetching channels
- Fetching a channel EPG
- Fetching autocomplete search results
- Fetching carousels