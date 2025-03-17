import { Box, Card, CardContent, Typography, List, ListItem, ListItemText, Divider } from '@mui/material';

interface NewsItem {
  id: number;
  title: string;
  [key: string]: any;
}

interface NewsListProps {
  news: NewsItem[];
  title?: string;
}

const NewsList = ({ news, title = '뉴스' }: NewsListProps) => {
  if (!news || news.length === 0) {
    return (
      <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <CardContent>
          <Typography variant="h6" component="h2" gutterBottom>
            {title}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            {title}가 없습니다.
          </Typography>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardContent>
        <Typography variant="h6" component="h2" gutterBottom>
          {title}
        </Typography>
        <List disablePadding>
          {news.map((item, index) => (
            <Box key={item.id}>
              {index > 0 && <Divider component="li" />}
              <ListItem alignItems="flex-start" disablePadding sx={{ py: 1 }}>
                <ListItemText
                  primary={item.title}
                  secondary={item.content || item.date || ''}
                />
              </ListItem>
            </Box>
          ))}
        </List>
      </CardContent>
    </Card>
  );
};

export default NewsList;