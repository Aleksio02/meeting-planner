ALTER TABLE invites
ADD CONSTRAINT un_invites_meetingid_userid UNIQUE (meeting_id, user_id);