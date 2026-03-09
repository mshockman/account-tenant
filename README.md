# Releasing API

This project is published through GitHub Actions when a version tag is pushed.

Example:

```bash
git tag api/v1.0.0 
git push origin api/v1.0.0
```

The build uses the tag name as the artifact version, with the leading v removed.
