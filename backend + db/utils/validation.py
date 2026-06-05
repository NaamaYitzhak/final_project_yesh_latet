def is_valid_email(email: str) -> bool:
    if (email.count("@") != 1):
        return False;

    local_part, domain_part = email.split("@")
    
    if not local_part or not domain_part:
        return False
    
    if "." not in domain_part:
        return False
    
    return True