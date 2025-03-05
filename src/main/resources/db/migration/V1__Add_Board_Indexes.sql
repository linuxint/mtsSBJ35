-- Add indexes for board-related tables to improve query performance

-- Indexes for TBL_BOARD
CREATE INDEX idx_board_deleteflag ON TBL_BOARD(DELETEFLAG);
CREATE INDEX idx_board_notice ON TBL_BOARD(BRDNOTICE, DELETEFLAG);
CREATE INDEX idx_board_bgno ON TBL_BOARD(BGNO, DELETEFLAG);
CREATE INDEX idx_board_userno ON TBL_BOARD(USERNO);

-- Indexes for TBL_BOARDREAD
CREATE INDEX idx_boardread_brdno ON TBL_BOARDREAD(BRDNO);

-- Indexes for TBL_BOARDFILE
CREATE INDEX idx_boardfile_brdno ON TBL_BOARDFILE(BRDNO, DELETEFLAG);

-- Indexes for TBL_BOARDREPLY
CREATE INDEX idx_boardreply_brdno ON TBL_BOARDREPLY(BRDNO, REDELETEFLAG);

-- Indexes for TBL_BOARDGROUP
CREATE INDEX idx_boardgroup_used ON TBL_BOARDGROUP(BGUSED, DELETEFLAG);