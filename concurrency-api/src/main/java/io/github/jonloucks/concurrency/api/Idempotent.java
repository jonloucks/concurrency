package io.github.jonloucks.concurrency.api;

/**
 * Idempotent state machine states
 */
public enum Idempotent implements TransitionAware {
    NEW {
        @Override
        public boolean canTransitionTo(String event, Object candidate) {
            return candidate == OPENED || candidate == OPENING || candidate == CLOSED || candidate == DESTROYED;
        }
    },
    OPENING {
        @Override
        public boolean canTransitionTo(String event, Object candidate) {
            return candidate == OPENED || candidate == CLOSED || candidate == DESTROYED;
        }
        @Override
        public boolean isRejecting() {
            return false;
        }
    },
    OPENED {
        @Override
        public boolean canTransitionTo(String event, Object candidate) {
            return candidate == CLOSING || candidate == CLOSED;
        }
        @Override
        public boolean isRejecting() {
            return false;
        }
    },
    CLOSING {
        @Override
        public boolean canTransitionTo(String event, Object candidate) {
            return candidate == CLOSED || candidate == DESTROYED;
        }
    },
    CLOSED {
        @Override
        public boolean canTransitionTo(String event, Object candidate) {
            return candidate == OPENED || candidate == OPENING || candidate == DESTROYED;
        }
    },
    DESTROYED;
    
    @Override
    public boolean canTransitionTo(String event, Object candidate) {
        return false;
    }
    
    public boolean isRejecting() {
        return true;
    }
}
