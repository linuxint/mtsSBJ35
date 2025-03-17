import { Box, Card, CardContent, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@mui/material';

interface NewsItem {
  id: number;
  title: string;
  brdno?: string;
  brdtitle?: string;
  usernm?: string;
  regdate?: string;
  bgname?: string;
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
            {`${title}가 없습니다.`}
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
        <TableContainer component={Paper} sx={{ boxShadow: 'none' }}>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell sx={{ fontWeight: 'bold', width: '40%' }}>제목</TableCell>
                <TableCell sx={{ fontWeight: 'bold', width: '20%' }}>등록자</TableCell>
                <TableCell sx={{ fontWeight: 'bold', width: '20%' }}>등록일</TableCell>
                <TableCell sx={{ fontWeight: 'bold', width: '20%' }}>위치</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {news.map((item) => (
                <TableRow key={item.id}>
                  <TableCell>{item.brdtitle || item.title}</TableCell>
                  <TableCell>{item.brdwriter || ''}</TableCell>
                  <TableCell>{item.regdate || ''}</TableCell>
                  <TableCell>{item.bgname || ''}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </CardContent>
    </Card>
  );
};

export default NewsList;
