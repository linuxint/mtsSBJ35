import React from 'react';
import { Box, Card, CardContent, Typography, List, ListItem, ListItemText, Divider } from '@mui/material';

interface TimelineItem {
  id: number;
  content: string;
  date?: string;
  time?: string;
  [key: string]: any;
}

interface TimelineProps {
  items: TimelineItem[];
}

const Timeline = ({ items }: TimelineProps) => {
  if (!items || items.length === 0) {
    return (
      <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <CardContent>
          <Typography variant="h6" component="h2" gutterBottom>
            타임라인
          </Typography>
          <Typography variant="body2" color="text.secondary">
            타임라인 항목이 없습니다.
          </Typography>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent>
        <Typography variant="h6" component="h2" gutterBottom>
          타임라인
        </Typography>
        <List disablePadding>
          {items.map((item, index) => (
            <Box key={item.id}>
              {index > 0 && <Divider component="li" />}
              <ListItem alignItems="flex-start" disablePadding sx={{ py: 1 }}>
                <ListItemText
                  primary={item.content}
                  secondary={
                    <React.Fragment key={`${item.id}-secondary`}>
                      {item.date && <span>{item.date} </span>}
                      {item.time && <span>{item.time}</span>}
                    </React.Fragment>
                  }
                />
              </ListItem>
            </Box>
          ))}
        </List>
      </CardContent>
    </Card>
  );
};

export default Timeline;
