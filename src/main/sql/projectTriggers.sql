CREATE OR REPLACE FUNCTION delete_expired_tokens()
    RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM players_tokens
    WHERE last_used_at < CURRENT_TIMESTAMP - INTERVAL '30 minutes';
    IF FOUND THEN
        RETURN NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_token_expiry
    BEFORE UPDATE ON players_tokens
    FOR EACH ROW
EXECUTE FUNCTION delete_expired_tokens();