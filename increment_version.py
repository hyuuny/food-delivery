def main():
    current_version = '1.0.0'

    try:
        major, minor, patch = map(int, current_version.split('.'))
    except ValueError:
        print(f"Error: Current version '{current_version}' is not in expected format.")
        return

    patch += 1
    new_version = f'{major}.{minor}.{patch}'
    print(f"::set-output name=new_version::{new_version}")

if __name__ == "__main__":
    main()
