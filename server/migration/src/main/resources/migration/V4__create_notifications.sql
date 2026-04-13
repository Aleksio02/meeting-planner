CREATE TABLE if not exists notifications
(
    id BIGSERIAL PRIMARY KEY,
    actor_id UUID NOT NULL REFERENCES users(id),
    receiver_id UUID NOT NULL REFERENCES users(id),
    meeting_id UUID REFERENCES meetings(id) ON DELETE SET NULL,
    invite_id UUID REFERENCES invites(id) ON DELETE SET NULL,
    type VARCHAR NOT NULL,
    sent_at TIMESTAMP NOT NULL
)