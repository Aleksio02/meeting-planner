ALTER TABLE public.meetings
    ADD COLUMN cancellation_reason VARCHAR(255),
ADD COLUMN cancelled_at TIMESTAMP;

ALTER TABLE public.notifications
    ADD COLUMN comment VARCHAR(255);