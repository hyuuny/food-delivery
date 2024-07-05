import os

def main():
    # Read current version from a file or environment variable
    current_version = os.getenv('CURRENT_VERSION', '1.0.0')

    # Parse version components
    major, minor, patch = map(int, current_version.split('.'))
    # Increment patch version
    patch += 1

    # Create new version string
    new_version = f'{major}.{minor}.{patch}'

    # Output the new version to GitHub Actions output
    print(f"::set-output name=new_version::{new_version}")

if __name__ == "__main__":
    main()
