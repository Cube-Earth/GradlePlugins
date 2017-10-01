package earth.cube.gradle.plugins.github.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gradle.api.logging.Logger;

import earth.cube.gradle.plugins.github.config.GithubReleaseConfig;
import earth.cube.gradle.plugins.github.utils.Encoder;
import earth.cube.gradle.plugins.github.utils.UrlAction;

public class GithubRelease {
		
	private boolean _bReady;
	private String _sUploadUrl;
	private Map<String, String> _assets = new HashMap<>();
	private Map<String, String> _assetNames = new HashMap<>();
	private GithubRepository _repository;
	private GithubReleaseConfig _config;
	private Logger _logger;
	
	
	public GithubRelease(GithubRepository repository) {
		_repository = repository;
	}
	
	public void setConfig(GithubReleaseConfig config) {
		_config = config;
	}
	
	public void setLogger(Logger logger) {
		_logger = logger;
	}
	
	
	public String toJson() {
		return String.format("{"
			  + "\"tag_name\": \"%s\","
			  + "\"target_commitish\": \"%s\","
			  + "\"name\": \"%s\","
			  + "\"body\": \"%s\","
			  + "\"draft\": %s,"
			  + "\"prerelease\": %s"
			+ "}",
			Encoder.encodeJsonString(_config.tagName),
			Encoder.encodeJsonString(_config.targetCommitish),
			Encoder.encodeJsonString(_config.name == null ? _config.tagName : _config.name),
			Encoder.encodeJsonString(_config.description),
			_config.draft,
			_config.preRelease
		);
	}
	
	@SuppressWarnings("unchecked")
	private boolean fetch() throws IOException {
		if(_bReady)
			return true;
		String sUrl = String.format("https://api.github.com/repos/%s/%s/releases/tags/%s", _repository.getOwnerName(), _repository.getRepositoryName(), _config.tagName);
		Map<String,Object> params = UrlAction.doGet(sUrl);
		if((boolean) params.get("#ResponseOk")) {
			String s = (String) params.get("upload_url");
			_sUploadUrl = s.replaceFirst("\\{.*\\}$", "");
			for(Map<String,Object> asset : (List<Map<String,Object>>) params.get("assets")) {
				_assets.put((String) asset.get("name"), asset.get("id").toString());
				_assets.put((String) asset.get("label"), asset.get("id").toString());
				_assetNames.put(asset.get("id").toString(), (String) asset.get("name"));
			}
				
			_bReady = true;
			return true;
		}
		return false;
	}
	
	private boolean create() throws IOException {
		if(_bReady)
			return false;
		String sUrl = String.format("https://api.github.com/repos/%s/%s/releases", _repository.getOwnerName(), _repository.getRepositoryName());
		Map<String,Object> params = UrlAction.doPost(sUrl, toJson(), UrlAction.combine(_repository.getUserEmail(), ':', _repository.getUserPwd()));
		if((boolean) params.get("#ResponseOk")) {
			_logger.quiet("release '{}' created.", _config.name);
			return true;
		}
		UrlAction.raiseException(params);
		return false;
	}
	
	
	private void setDefaultValues() {
		if(_config.name == null || _config.name.length() == 0)
			_config.name = _config.tagName;
	}

	private void validate() {
		setDefaultValues();
		if(_config.tagName == null || _config.tagName.length() == 0)
			throw new IllegalArgumentException("Release tag name is mandatory!");
	}

	public void ensure() throws IOException {
		validate();
		if(!fetch())
			if(create())
				if(!fetch())
					throw new IllegalStateException("After release creation, the newly-created release can not be fetched!");
	}
	
	public String getAssetUploadUrl() {
		return _sUploadUrl;
	}
	
	public String getAssetId(String... saNameOrLabel) {
		for(String sNameOrLabel : saNameOrLabel) {
			String sId = _assets.get(sNameOrLabel);
			if(sId != null)
				return sId;
		}
		return null;
	}

	public void deleteAsset(String sId) throws IOException {
		if(!_bReady)
			throw new IllegalStateException("Not initialized! Please call 'fetch' or 'ensure' first!");
		String sUrl = String.format("https://api.github.com/repos/%s/%s/releases/assets/%s", _repository.getOwnerName(), _repository.getRepositoryName(), sId);
		Map<String,Object> params = UrlAction.doAction("DELETE", sUrl, null, UrlAction.combine(_repository.getUserEmail(), ':', _repository.getUserPwd()));
		if((boolean) params.get("#ResponseOk")) {
			_logger.quiet("Asset '{}' deleted from github.", _assetNames.get(sId));
			for(Entry<String, String> e : new HashSet<Map.Entry<String, String>>(_assets.entrySet()))
				if(e.getValue().equals(sId))
					_assets.remove(e.getKey());
			_assetNames.remove(sId);
		}
		else
			UrlAction.raiseException(params);
	}
	
	private void doUploadAsset(String sName, String sLabel, File file) throws IOException {
		if(!_bReady)
			throw new IllegalStateException("Not initialized! Please call 'fetch' or 'ensure' first!");
		
		String sUrl = String.format("%s?name=%s&label=%s", _sUploadUrl, Encoder.encodeUrlString(sName), Encoder.encodeUrlString(sLabel));
		Map<String,Object> params = UrlAction.doAction("POST", sUrl, file, UrlAction.combine(_repository.getUserEmail(), ':', _repository.getUserPwd()));
		if((boolean) params.get("#ResponseOk")) {
			_logger.quiet("Asset '{}' uploaded to github.", sName);
			String sId = params.get("id").toString();
			_assets.put(sName, sId);
			_assets.put(sLabel, sId);
			_assetNames.put(sId, sName);
		}
		else
			UrlAction.raiseException(params);
	}	

	public void uploadAsset(String sName, String sLabel, File file, boolean bReplace) throws IOException {
		String sId = getAssetId(sName, sLabel);
		if(bReplace) {
			if(sId != null)
				deleteAsset(sId);
		}
		else
			if(sId != null) {
				_logger.warn(String.format("Asset with name '{}' and name '{}' already exists. skipping ...", sName, sLabel));
				return;
			}
		doUploadAsset(sName, sLabel, file);
	}

	
}
